package com.example.regapp.repository;

import com.example.regapp.entity.Privilege;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PriviledgeRepository extends JpaRepository<Privilege, Long> {
    Privilege findPriviledgeByFileNameAndUserIdentifier(String fileName, String userIdentifier);
}
