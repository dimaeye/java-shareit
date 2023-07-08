package ru.practicum.shareit.user.repository.inmemory;//package ru.practicum.shareit.user.repository.inmemory;
//
//import org.springframework.stereotype.Component;
//import ru.practicum.shareit.user.exception.UserNotFoundException;
//import ru.practicum.shareit.user.model.User;
//import ru.practicum.shareit.user.repository.UserRepository;
//
//import java.util.*;
//import java.util.concurrent.atomic.AtomicInteger;
//
//@Component
//public class InMemoryUserRepository implements UserRepository {
//    private final Map<Integer, User> users = new HashMap<>();
//    private final AtomicInteger uniqueUserId = new AtomicInteger(0);
//
//    @Override
//    public User save(User user) {
//        user.setId(uniqueUserId.incrementAndGet());
//        users.put(user.getId(), user);
//        return user;
//    }
//
//    @Override
//    public User update(User user) throws UserNotFoundException {
//        User userForUpdate = users.get(user.getId());
//        if (userForUpdate == null)
//            throw new UserNotFoundException(user.getId());
//
//        if (user.getEmail() != null)
//            userForUpdate.setEmail(user.getEmail());
//        if (user.getName() != null)
//            userForUpdate.setName(user.getName());
//
//        return userForUpdate;
//    }
//
//    @Override
//    public Optional<User> findById(int id) {
//        User user = users.get(id);
//
//        return Optional.ofNullable(user);
//    }
//
//    @Override
//    public Optional<User> findByEmail(String email) {
//        return users.values().stream().filter(user -> user.getEmail().equals(email)).findFirst();
//    }
//
//    @Override
//    public List<User> findAll() {
//        return new ArrayList<>(users.values());
//    }
//
//    @Override
//    public void delete(User user) {
//        if (users.remove(user.getId()) == null)
//            throw new UserNotFoundException(user.getId());
//    }
//}
