package com.vilia.miarrobawebscrapper.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.vilia.miarrobawebscrapper.model.MiarrobaUser;

@Repository
public interface MiarrobaUserRepository extends JpaRepository<MiarrobaUser, Long> {

}
