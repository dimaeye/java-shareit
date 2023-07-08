package ru.practicum.shareit.item.repository.inmemory;//package ru.practicum.shareit.item.repository.inmemory;
//
//import org.springframework.stereotype.Component;
//import ru.practicum.shareit.item.exception.ItemNotFoundException;
//import ru.practicum.shareit.item.model.Item;
//import ru.practicum.shareit.item.repository.ItemRepository;
//import ru.practicum.shareit.user.exception.UserNotFoundException;
//
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.List;
//import java.util.Optional;
//import java.util.concurrent.atomic.AtomicInteger;
//import java.util.stream.Collectors;
//
//@Component
//public class InMemoryItemRepository implements ItemRepository {
//    private final List<Item> items = new ArrayList<>();
//    private final AtomicInteger uniqueItemId = new AtomicInteger(0);
//
//    @Override
//    public Item save(Item item) {
//        item.setId(uniqueItemId.incrementAndGet());
//        items.add(item);
//        return item;
//    }
//
//    @Override
//    public Item update(Item item) throws ItemNotFoundException {
//        Item itemForUpdate = findById(item.getId())
//                .orElseThrow(() -> new ItemNotFoundException(item.getId()));
//
//        if (item.getName() != null)
//            itemForUpdate.setName(item.getName());
//        if (item.getDescription() != null)
//            itemForUpdate.setDescription(item.getDescription());
//        if (item.getAvailable() != null)
//            itemForUpdate.setAvailable(item.getAvailable());
//
//        return itemForUpdate;
//    }
//
//    @Override
//    public Optional<Item> findById(int id) {
//        return items.stream()
//                .filter(item -> item.getId() == id)
//                .findFirst();
//    }
//
//    @Override
//    public List<Item> findByOwnerId(int ownerId) throws UserNotFoundException {
//        return items.stream()
//                .filter(item -> item.getOwner().getId() == ownerId)
//                .collect(Collectors.toList());
//    }
//
//    @Override
//    public List<Item> findAvailableByNameOrDescription(String text) {
//        if (text == null || text.isBlank())
//            return Collections.emptyList();
//        else {
//            String lowerText = text.toLowerCase();
//            return items.stream()
//                    .filter(item -> (item.getName().toLowerCase().contains(lowerText)
//                            || item.getDescription().toLowerCase().contains(lowerText))
//                            && item.getAvailable())
//                    .collect(Collectors.toList());
//        }
//    }
//}
