package com.practice.shareitzeinolla.request;

import com.practice.shareitzeinolla.exception.NotFoundException;
import com.practice.shareitzeinolla.user.User;
import com.practice.shareitzeinolla.user.UserJpaRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

@AutoConfigureTestDatabase
@DataJpaTest
public class RequestRepositoryTest {
    @Autowired
    RequestJpaRepository requestRepository;
    @Autowired
    UserJpaRepository userRepository;

    @Test
    void saveRequest_shouldSave_whenDescriptionIsGiven() {
        Request request = new Request("RepositoryTestSave");
        requestRepository.save(request);

        Request savedRequest = requestRepository.findById(request.getId()).orElseThrow();
        Assertions.assertEquals(request.getDescription(), savedRequest.getDescription());
    }

    @Test
    void findById_shouldFind_whenRequestExists() {
        Request request = new Request("RepositoryTestFindById1");
        requestRepository.save(request);

        Request foundRequest = requestRepository.findById(request.getId()).orElseThrow();
        Assertions.assertEquals(request.getId(), foundRequest.getId());
    }

    @Test
    void findById_shouldNotFind_whenRequestDoesNotExist() {
        Request request = new Request("RepositoryTestFindById2");
        requestRepository.save(request);
        String expectedMessage = "Запрос не найден";
        Long notExistingId = 100L;

        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> requestRepository.findById(notExistingId)
                        .orElseThrow(() -> new NotFoundException(expectedMessage))
        );

        Assertions.assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    void findAll_shouldFindAll_whenRequestsExists() {
        List<Request> requests = List.of(
                new Request("RepositoryTestFindAll1"),
                new Request("RepositoryTestFindAll2"),
                new Request("RepositoryTestFindAll3")
        );
        int expectedSize = requests.size();
        requestRepository.saveAll(requests);

        List<Request> foundRequests = requestRepository.findAll();

        Assertions.assertEquals(expectedSize, foundRequests.size());
    }

    @Test
    void findAll_shouldNotFindAll_whenRequestsDoesNotExist() {
        int expectedSize = 0;

        List<Request> foundRequests = requestRepository.findAll();

        Assertions.assertEquals(expectedSize, foundRequests.size());
    }

    @Test
    void deleteById_shouldDelete_whenRequestExists() {
        long id = 1;
        String expectedMessage = "Запрос не найден";
        Request request = new Request("RepositoryTestDeleteById");
        request.setId(id);
        requestRepository.save(request);

        requestRepository.deleteById(request.getId());
        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> requestRepository.findById(id)
                        .orElseThrow(() -> new NotFoundException(expectedMessage))
        );

        Assertions.assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    void findAll_shouldFindAll_whenUserExistsAndRequestsExist() {
        User user = new User("RepositoryTestFindAll", "repository5@findall.com");
        userRepository.save(user);

        Request request1 = new Request("RepositoryTestFindAll4");
        request1.setUser(user);
        Request request2 = new Request("RepositoryTestFindAll5");
        request2.setUser(user);
        requestRepository.save(request1);
        requestRepository.save(request2);
        int expectedSize = 2;

        List<Request> foundRequests = requestRepository.findAllByUserIdOrderByCreatedDesc(user.getId());

        Assertions.assertEquals(expectedSize, foundRequests.size());
    }

    @Test
    void findAll_shouldNotFind_whenUserExistsAndRequestsDoNotExist() {
        User user = new User("RepositoryTestFindAll", "repository6@findall.com");
        userRepository.save(user);
        int expectedSize = 0;

        List<Request> foundRequests = requestRepository.findAllByUserIdOrderByCreatedDesc(user.getId());

        Assertions.assertEquals(expectedSize, foundRequests.size());
    }

    @Test
    void findAll_shouldNotFind_whenUserDoesNotExist() {
        long notExistingId = 100L;
        int expectedSize = 0;

        List<Request> foundRequests = requestRepository.findAllByUserIdOrderByCreatedDesc(notExistingId);

        Assertions.assertEquals(expectedSize, foundRequests.size());
    }

    @Test
    void findAll_shouldFindAllExceptUser_whenRequestsExist() {
        User user = new User("RepositoryTestFindAll", "repository7@findall.com");
        userRepository.save(user);
        User otherUser = new User("RepositoryTestFindAll", "repository8@findall.com");
        userRepository.save(otherUser);
        Request request1 = new Request("RepositoryTestFindAll4");
        request1.setUser(otherUser);
        Request request2 = new Request("RepositoryTestFindAll5");
        request2.setUser(otherUser);
        requestRepository.save(request1);
        requestRepository.save(request2);
        int expectedSize = 2;
        Request request = new Request("RepositoryTestFindAll7");
        request.setUser(user);
        requestRepository.save(request);
        Pageable pageable = PageRequest.of(0, 10);

        List<Request> foundRequests = requestRepository.findAllExceptUserId(user.getId(), pageable);

        Assertions.assertEquals(expectedSize, foundRequests.size());
    }

    @Test
    void findAll_shouldNotFindAllExceptUser_whenRequestsDoNotExist() {
        User user = new User("RepositoryTestFindAll", "repository9@findall.com");
        userRepository.save(user);
        Request request = new Request("RepositoryTestFindAll8");
        request.setUser(user);
        requestRepository.save(request);
        Pageable pageable = PageRequest.of(0, 10);
        int expectedSize = 0;

        List<Request> foundRequests = requestRepository.findAllExceptUserId(user.getId(), pageable);

        Assertions.assertEquals(expectedSize, foundRequests.size());
    }
}