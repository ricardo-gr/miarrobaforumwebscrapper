package com.vilia.miarrobawebscrapper.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.vilia.miarrobawebscrapper.model.MiarrobaForum;

@Repository
public interface MiarrobaForumRepository extends JpaRepository<MiarrobaForum, Long> {

}
