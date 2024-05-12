package com.vilia.miarrobawebscrapper.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MiarrobaMessageRepository extends JpaRepository<com.vilia.miarrobawebscrapper.model.MiarrobaMessage, Long> {

}
