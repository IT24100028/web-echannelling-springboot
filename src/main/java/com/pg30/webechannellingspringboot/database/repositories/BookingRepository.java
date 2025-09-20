package com.pg30.webechannellingspringboot.database.repositories;

import com.pg30.webechannellingspringboot.entities.BookingEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingRepository extends JpaRepository<BookingEntity, Long> {

}
