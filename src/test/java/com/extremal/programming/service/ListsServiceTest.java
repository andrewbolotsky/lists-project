package com.extremal.programming.service;

import static org.mockito.Mockito.when;

import com.extremal.programming.entity.ListEntity;
import com.extremal.programming.entity.ListNode;
import com.extremal.programming.entity.User;
import com.extremal.programming.repository.ListsRepository;
import com.extremal.programming.repository.UserRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.web.server.ResponseStatusException;

public class ListsServiceTest {

  private ListsRepository listsRepository;
  private UserRepository userRepository;
  private ListsService listsService;

  @BeforeEach
  public void setUp() {
    listsRepository = Mockito.mock(ListsRepository.class);
    userRepository = Mockito.mock(UserRepository.class);
    listsService = new ListsService(userRepository, listsRepository);
  }

  @Test
  public void getListEntitiesForConcreteUser_userExistsWithLists_ReturnLists() {
    User expectedUser = new User();
    ListEntity expectedList = new ListEntity();
    expectedList.setName("Test List");
    ListNode listNode = new ListNode();
    listNode.setValue("Test Node");
    expectedList.setNodes(List.of(listNode));
    expectedUser.setLists(List.of(expectedList));

    when(userRepository.findByUsername("user")).thenReturn(Optional.of(expectedUser));

    List<ListEntity> actualLists = listsService.getListEntitiesForConcreteUser("user");

    Assertions.assertEquals(1, actualLists.size());
    Assertions.assertEquals("Test List", actualLists.get(0).getName());
    Assertions.assertEquals("Test Node", actualLists.get(0).getNodes().get(0).getValue());
  }

  @Test
  public void getListEntitiesForConcreteUser_userDoesNotExist_ThrowsException() {
    when(userRepository.findByUsername("user")).thenReturn(Optional.empty());

    ResponseStatusException exception = Assertions.assertThrows(ResponseStatusException.class,
        () -> {
          listsService.getListEntitiesForConcreteUser("user");
        });

    Assertions.assertEquals("404 NOT_FOUND \"User with name user not found\"",
        exception.getMessage());
  }

  @Test
  public void getListById_listExists_ReturnsList() {
    ListEntity expectedList = new ListEntity();
    expectedList.setName("Sample List");

    when(listsRepository.findById(1L)).thenReturn(Optional.of(expectedList));

    ListEntity actualList = listsService.getListById(1L);

    Assertions.assertEquals("Sample List", actualList.getName());
  }

  @Test
  public void getListById_listDoesNotExist_ThrowsException() {
    when(listsRepository.findById(1L)).thenReturn(Optional.empty());

    ResponseStatusException exception = Assertions.assertThrows(ResponseStatusException.class,
        () -> {
          listsService.getListById(1L);
        });

    Assertions.assertEquals("404 NOT_FOUND \"List with id 1 not found\"", exception.getMessage());
  }

  @Test
  public void getUserByUsernameAndPassword_userExistsAndPasswordMatches_ReturnsUser() {
    User expectedUser = new User();
    expectedUser.setUsername("user");
    expectedUser.setPassword("pass");

    when(userRepository.findByUsername("user")).thenReturn(Optional.of(expectedUser));

    User actualUser = listsService.getUserByUsernameAndPassword("user", "pass");

    Assertions.assertEquals("user", actualUser.getUsername());
  }

  @Test
  public void getUserByUsernameAndPassword_userExistsButPasswordDoesNotMatch_ThrowsException() {
    User expectedUser = new User();
    expectedUser.setUsername("user");
    expectedUser.setPassword("pass");

    when(userRepository.findByUsername("user")).thenReturn(Optional.of(expectedUser));

    ResponseStatusException exception = Assertions.assertThrows(ResponseStatusException.class,
        () -> {
          listsService.getUserByUsernameAndPassword("user", "wrongpass");
        });

    Assertions.assertEquals(
        "403 FORBIDDEN \"User with name user and password wrongpass not match\"",
        exception.getMessage());
  }

  @Test
  public void createNewUser_userExistsWithDifferentPassword_ThrowsException() {
    User existingUser = new User();
    existingUser.setUsername("user");
    existingUser.setPassword("oldpass");

    when(userRepository.findByUsername("user")).thenReturn(Optional.of(existingUser));

    ResponseStatusException exception = Assertions.assertThrows(ResponseStatusException.class,
        () -> {
          listsService.createNewUser("user", "newpass");
        });

    Assertions.assertEquals(
        "400 BAD_REQUEST \"User with such username already exists and passwords differ\"",
        exception.getMessage());
  }

  @Test
  public void createNewList_ListCreatedSuccessfully_ReturnsList() {
    ListEntity listEntity = new ListEntity();
    listEntity.setName("New List");

    when(listsRepository.save(Mockito.any(ListEntity.class))).thenReturn(listEntity);

    ListEntity createdList = listsService.createNewList("New List");

    Assertions.assertEquals("New List", createdList.getName());
  }

  @Test
  public void addNodeToList_listExists_NodeAdded() {
    ListEntity listEntity = new ListEntity();
    listEntity.setNodes(new ArrayList<>());

    when(listsRepository.findById(1L)).thenReturn(Optional.of(listEntity));
    when(listsRepository.save(Mockito.any(ListEntity.class))).thenReturn(listEntity);

    ListEntity updatedList = listsService.addNodeToList(1L, "New Node");

    Assertions.assertEquals(1, updatedList.getNodes().size());
    Assertions.assertEquals("New Node", updatedList.getNodes().getFirst().getValue());
  }

  @Test
  public void addNodeToList_listDoesNotExist_ThrowsException() {
    when(listsRepository.findById(1L)).thenReturn(Optional.empty());

    ResponseStatusException exception = Assertions.assertThrows(ResponseStatusException.class,
        () -> {
          listsService.addNodeToList(1L, "New Node");
        });

    Assertions.assertTrue(exception.getMessage().startsWith("404"));
  }
}
