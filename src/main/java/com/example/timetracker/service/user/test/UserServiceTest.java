package com.example.timetracker.service.user.test;

import com.example.timetracker.repository.UserRepository;
import com.example.timetracker.service.user.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class UserServiceTest {

    @Test
    void testRegister() {

        UserRepository repo = Mockito.mock(UserRepository.class);

        UserService service = new UserService(repo, null);

        service.register("test", "1234");

        Mockito.verify(repo).save(Mockito.any());
    }
}