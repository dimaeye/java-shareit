package ru.practicum.shareit.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public User createUser(User user) {
        if (user.getName() == null)
            user.setName(user.getEmail());

        return userRepository.save(user);
    }

    @Override
    @Transactional
    public User updateUser(User user) throws UserNotFoundException {
        User userForUpdate = userRepository.findById(user.getId())
                .orElseThrow(() -> new UserNotFoundException(user.getId()));
        if (user.getEmail() != null)
            userForUpdate.setEmail(user.getEmail());
        if (user.getName() != null)
            userForUpdate.setName(user.getName());

        return userRepository.save(userForUpdate);
    }

    @Override
    @Transactional(readOnly = true)
    public User getUser(int userId) throws UserNotFoundException {
        return userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    @Transactional
    public void deleteUser(int userId) throws UserNotFoundException {
        User user = getUser(userId);

        userRepository.delete(user);
    }
}
