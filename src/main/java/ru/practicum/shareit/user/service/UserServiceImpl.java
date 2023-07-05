package ru.practicum.shareit.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.exception.DuplicateEmailException;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User createUser(User user) throws DuplicateEmailException {
        if (user.getName() == null)
            user.setName(user.getEmail());

        if (userRepository.findByEmail(user.getEmail()).isPresent())
            throw new DuplicateEmailException("Пользователь с таким же email " + user.getEmail() + " уже создан");
        else
            return userRepository.save(user);
    }

    @Override
    public User updateUser(User user) throws UserNotFoundException, DuplicateEmailException {
        Optional<User> userFindByEmail =
                user.getEmail() != null ? userRepository.findByEmail(user.getEmail()) : Optional.empty();

        if (userFindByEmail.isEmpty() || userFindByEmail.get().getId() == user.getId())
            return userRepository.update(user);
        else
            throw new DuplicateEmailException(
                    "Не удалось обновить почту "
                            + user.getEmail() + " у пользователя, т.к. уже используется другим пользователем"
            );
    }

    @Override
    public User getUser(int userId) throws UserNotFoundException {
        return userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public void deleteUser(int userId) throws UserNotFoundException {
        User user = getUser(userId);

        userRepository.delete(user);
    }
}
