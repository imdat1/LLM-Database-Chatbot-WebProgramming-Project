package com.example.sql_chatbot.Service.Impl;

import com.example.sql_chatbot.Models.User;
import com.example.sql_chatbot.Models.enumerations.Role;
import com.example.sql_chatbot.Models.exceptions.InvalidUsernameOrPasswordException;
import com.example.sql_chatbot.Models.exceptions.PasswordsDoNotMatchException;
import com.example.sql_chatbot.Models.exceptions.UsernameAlreadyExistsException;
import com.example.sql_chatbot.Repository.UserRepository;
import com.example.sql_chatbot.Service.UserService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User register(String username, String password, String repeatPassword, String huggingFaceAPIToken, Role role) {
        if (username == null || password == null || username.isEmpty() || password.isEmpty()) {
            throw new InvalidUsernameOrPasswordException();
        }

        if (!password.equals(repeatPassword)) {
            throw new PasswordsDoNotMatchException();
        }

        if(this.userRepository.findByUsername(username).isPresent()) {
            throw new UsernameAlreadyExistsException(username);
        }

        User user = new User(
                username,
                passwordEncoder.encode(password),
                huggingFaceAPIToken,
                role
        );

        return userRepository.save(user);

    }

    @Override
    public List<User> findAll() {
        return this.userRepository.findAll();
    }

    @Override
    public User findByUsername(String username) {
        return this.userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException(username));
    }

    @Override
    public User deleteUser(User user) {
        this.userRepository.delete(user);
        return user;
    }

    @Override
    public User updateUsername(String oldUsername, String newUsername) {
        User user = userRepository.findByUsername(oldUsername).orElseThrow(()-> new UsernameNotFoundException(oldUsername));
        user.setUsername(newUsername);
        return this.userRepository.save(user);
    }

    @Override
    public User updateUserCredentials(String username, String huggingFaceAPIToken, Role role) {
        User user = this.userRepository.findByUsername(username).orElseThrow(()-> new UsernameNotFoundException(username));

        user.setHuggingFaceAPIToken(huggingFaceAPIToken);
        user.setRole(role);

        return this.userRepository.save(user);
    }

    @Override
    public User updateUserPassword(String username, String password, String repeatedPassword) {
        if (username == null || password == null || username.isEmpty() || password.isEmpty()) {
            throw new InvalidUsernameOrPasswordException();
        }

        if (!password.equals(repeatedPassword)) {
            throw new PasswordsDoNotMatchException();
        }
        User user = this.userRepository.findByUsername(username).orElseThrow(()-> new UsernameNotFoundException(username));

        user.setPassword(passwordEncoder.encode(password));

        return this.userRepository.save(user);
    }

    @Override
    public User updateUser(User user) {
        return this.userRepository.save(user);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException(username));
    }
}
