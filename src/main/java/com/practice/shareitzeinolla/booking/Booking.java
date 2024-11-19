package com.practice.shareitzeinolla.booking;

import com.practice.shareitzeinolla.item.Item;
import com.practice.shareitzeinolla.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "bookings")
public class Booking {
    public Booking() {
    }

    public Booking(Item item, LocalDateTime fromDate, LocalDateTime toDate) {
        this.item = item;
        this.fromDate = fromDate;
        this.toDate = toDate;
    }

    public Booking(User user, Item item, LocalDateTime fromDate, LocalDateTime toDate) {
        this.user = user;
        this.item = item;
        this.fromDate = fromDate;
        this.toDate = toDate;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "item_id")
    private Item item;

    @Enumerated
    @Column(name = "status")
    private BookingStatus status;

    @Column(name = "from_date")
    private LocalDateTime fromDate;

    @Column(name = "to_date")
    private LocalDateTime toDate;
}
