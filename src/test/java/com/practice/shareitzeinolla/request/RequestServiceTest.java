package com.practice.shareitzeinolla.request;

import com.practice.shareitzeinolla.exception.NotFoundException;
import com.practice.shareitzeinolla.user.User;
import com.practice.shareitzeinolla.user.UserJpaRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class RequestServiceTest {
    RequestJpaService requestService;

    @Mock
    RequestJpaRepository requestRepository;

    @Mock
    UserJpaRepository userRepository;

    @BeforeEach
    void createService() {
        requestService = new RequestJpaService(requestRepository, userRepository);
    }

    @Test
    void createRequest_shouldCreate_whenRequestCorrect() {
        User user = new User("ServiceTestCreate", "service1@create.com");
        user.setId(1L);

        Mockito.when(userRepository.findById(user.getId()))
                .thenReturn(Optional.of(user));

        Request request = new Request("ServiceTestCreate");

        Mockito.when(requestRepository.save(request))
                .thenReturn(request);

        Request requestCreated = requestService.create(request, user.getId());

        Assertions.assertNotNull(requestCreated);
        Assertions.assertEquals(user.getId(), requestCreated.getUser().getId());
        Assertions.assertEquals("ServiceTestCreate", requestCreated.getDescription());
    }

    @Test
    void createRequest_shouldNotCreate_whenUserDoesNotExist() {
        String expectedMessage = "Пользователь не найден";

        Mockito.when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.empty());

        Request request = new Request("ServiceTestCreate");

        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> requestService.create(request, 1L)
        );

        Assertions.assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    void findAllRequests_shouldFindAll_whenUserExistsAndRequestsExist() {
        User user = new User("ServiceTestFindAll", "service1@findall.com");
        user.setId(2L);

        Mockito.when(userRepository.findById(user.getId()))
                .thenReturn(Optional.of(user));

        List<Request> requests = List.of(
                new Request("ServiceTestFindAll1"),
                new Request("ServiceTestFindAll2")
        );
        int expectedSize = requests.size();

        Mockito.when(requestRepository.findAllByUserIdOrderByCreatedDesc(Mockito.anyLong()))
                .thenReturn(requests);

        List<Request> foundRequests = requestService.findAll(user.getId());

        Assertions.assertEquals(expectedSize, foundRequests.size());
    }

    @Test
    void findAllRequests_shouldNotFindAll_whenUserDoesNotExist() {
        String expectedMessage = "Пользователь не найден";
        long notExistingId = 100L;
        Mockito.when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.empty());

        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> requestService.findAll(notExistingId)
        );

        Assertions.assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    void findByIdRequest_shouldFind_whenRequestExists() {
        Request request = new Request("ServiceTestFindById1");
        request.setId(1L);

        Mockito.when(requestRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(request));

        Request requestFound = requestService.findById(request.getId());

        Assertions.assertNotNull(requestFound);
        Assertions.assertEquals(request.getId(), requestFound.getId());
        Assertions.assertEquals(request.getDescription(), requestFound.getDescription());
    }

    @Test
    void findByIdRequest_shouldNotFind_whenRequestDoesNotExist() {
        long notExistingId = 100L;
        String expectedMessage = "Запрос не найден";

        Mockito.when(requestRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.empty());

        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> requestService.findById(notExistingId)
        );

        Assertions.assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    void findAllOtherUsers_shouldFindAll_whenRequestsExist() {
        List<Request> requests = List.of(
                new Request("ServiceTestFindAllOtherUsers1"),
                new Request("ServiceTestFindAllOtherUsers2")
        );
        int expectedSize = requests.size();

        Mockito.when(requestRepository.findAllExceptUserId(Mockito.anyLong(), Mockito.any(Pageable.class)))
                .thenReturn(requests);

        List<Request> foundRequests = requestService.findAllOtherUsers(1L, 0, 10);

        Assertions.assertNotNull(foundRequests);
        Assertions.assertEquals(expectedSize, foundRequests.size());
    }
}
