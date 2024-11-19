package com.practice.shareitzeinolla.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@AutoConfigureTestDatabase
@DataJpaTest
public class BookingRepositoryTest {
    @Autowired
    BookingJpaRepository bookingRepository;
}
