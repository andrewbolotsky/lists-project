package com.extremal.programming.service;

import com.extremal.programming.entity.ListEntity;
import com.extremal.programming.entity.ListNode;
import com.extremal.programming.entity.User;
import com.extremal.programming.repository.ListsRepository;
import com.extremal.programming.repository.UserRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class ListsService {
  private final UserRepository userRepository;
  private final ListsRepository listsRepository;
  public List<ListEntity> getListEntitiesForConcreteUser(String username) {
    Optional<User> userOptional = userRepository.findByUsername(username);
    if (userOptional.isEmpty()) {
      throw new ResponseStatusException(HttpStatusCode.valueOf(404),
          String.format("User with name %s not found", username));
    }
    List<ListEntity> lists = userOptional.get().getLists();
    return lists;
  }
  public ListEntity getListById(Long id) {
    Optional<ListEntity> listEntity = listsRepository.findById(id);
    if (listEntity.isEmpty()) {
      throw new ResponseStatusException(HttpStatusCode.valueOf(404),
          String.format("List with id %d not found", id));
    }
    return listEntity.get();
  }

  public User getUserByUsernameAndPassword(String username, String password) {
    Optional<User> userOptional = userRepository.findByUsername(username);
    if (userOptional.isEmpty()) {
      throw new ResponseStatusException(HttpStatusCode.valueOf(404),
          String.format("User with name %s not found", username));
    }
    User user = userOptional.get();
    if (!user.getPassword().equals(password)) {
      throw new ResponseStatusException(HttpStatusCode.valueOf(403),String.format("User with name %s and password %s not match", username, password));
    }
    return user;
  }

  public User createNewUser(String username, String password) {
    User user = new User();
    Optional<User> userOptional = userRepository.findByUsername(username);
    if (userOptional.isPresent() && !userOptional.get().getPassword().equals(password)) {
      throw new ResponseStatusException(HttpStatusCode.valueOf(400),
          "User with such username already exists and passwords differ");
    }
    user.setUsername(username);
    user.setPassword(password);
    return userRepository.save(user);
  }

  public ListEntity createNewList(String listName) {
    ListEntity listEntity = new ListEntity();
    listEntity.setName(listName);
    return listsRepository.save(listEntity);
  }

  public ListEntity addNodeToList(Long id, String nodeValue) {
    Optional<ListEntity> listEntityOptional = listsRepository.findById(id);
    if (listEntityOptional.isEmpty()) {
      throw new ResponseStatusException(HttpStatusCode.valueOf(404),
          "No such list exist");
    }
    ListEntity listEntity = listEntityOptional.get();
    if (listEntity.getNodes() == null){
      listEntity.setNodes(new ArrayList<>());
    }
    ListNode node = new ListNode();
    node.setValue(nodeValue);
    listEntity.getNodes().add(node);
    return listsRepository.save(listEntity);
  }


}
