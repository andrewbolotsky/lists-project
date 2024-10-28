package com.extremal.programming.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.extremal.programming.entity.ListEntity;
import com.extremal.programming.entity.ListNode;
import com.extremal.programming.entity.User;
import com.extremal.programming.repository.ListsRepository;
import com.extremal.programming.repository.UserRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

public class ListsServiceTest {

  private ListsRepository listsRepository;
  private UserRepository userRepository;
  private ListsService listsService;

  @BeforeEach
  public void setUp() {
    listsRepository = mock(ListsRepository.class);
    userRepository = mock(UserRepository.class);
    listsService = new ListsService(userRepository, listsRepository);
  }

  @Test
  public void getListEntitiesForConcreteUser_userExistsWithLists_ReturnsLists() {
    User user = new User();
    ListEntity listEntity = new ListEntity();
    ListNode listNode = new ListNode();
    listNode.setValue("testNode");
    listEntity.setNodes(List.of(listNode));
    user.setLists(List.of(listEntity));
    when(userRepository.findByUsername("user")).thenReturn(Optional.of(user));
    List<ListEntity> actualLists = listsService.getListEntitiesForConcreteUser("user");
    assertEquals(1, actualLists.size());
    assertEquals(1, actualLists.get(0).getNodes().size());
    assertEquals("testNode", actualLists.get(0).getNodes().get(0).getValue());
  }

  @Test
  public void getListEntitiesForConcreteUser_userNotFound_ThrowsException() {
    when(userRepository.findByUsername("unknownUser")).thenReturn(Optional.empty());
    ResponseStatusException exception = assertThrows(ResponseStatusException.class,
        () -> listsService.getListEntitiesForConcreteUser("unknownUser"));
    assertEquals("404 NOT_FOUND \"User with name unknownUser not found\"", exception.getMessage());
  }

  @Test
  public void getListById_listExists_ReturnsListEntity() {
    ListEntity listEntity = new ListEntity();
    listEntity.setName("Sample List");
    when(listsRepository.findById(1L)).thenReturn(Optional.of(listEntity));
    ListEntity result = listsService.getListById(1L);
    assertEquals("Sample List", result.getName());
  }

  @Test
  public void getListById_listDoesNotExist_ThrowsException() {
    when(listsRepository.findById(1L)).thenReturn(Optional.empty());
    ResponseStatusException exception = assertThrows(ResponseStatusException.class,
        () -> listsService.getListById(1L));
    assertEquals("404 NOT_FOUND \"List with id 1 not found\"", exception.getMessage());
  }

  @Test
  public void createNewList_createsAndReturnsListEntity() {
    ListEntity listEntity = new ListEntity();
    listEntity.setName("New List");
    when(listsRepository.save(any(ListEntity.class))).thenReturn(listEntity);
    ListEntity result = listsService.createNewList("New List");
    assertEquals("New List", result.getName());
  }

  @Test
  public void addNodeToList_listExists_AddsNode() {
    ListEntity listEntity = new ListEntity();
    listEntity.setNodes(new ArrayList<>());
    when(listsRepository.findById(1L)).thenReturn(Optional.of(listEntity));
    when(listsRepository.save(any(ListEntity.class))).thenReturn(listEntity);

    ListEntity result = listsService.addNodeToList(1L, "newNode");
    assertEquals(1, result.getNodes().size());
    assertEquals("newNode", result.getNodes().get(0).getValue());
  }

  @Test
  public void addNodeToList_listDoesNotExist_ThrowsException() {
    when(listsRepository.findById(1L)).thenReturn(Optional.empty());
    ResponseStatusException exception = assertThrows(ResponseStatusException.class,
        () -> listsService.addNodeToList(1L, "newNode"));
    assertEquals("404 NOT_FOUND \"No such list exist\"", exception.getMessage());
  }
}
