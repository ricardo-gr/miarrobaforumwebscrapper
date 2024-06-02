package com.vilia.miarrobawebscrapper.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.vilia.miarrobawebscrapper.model.MiarrobaThread;

@Repository
public interface MiarrobaThreadRepository extends JpaRepository<MiarrobaThread, Long> {

}
