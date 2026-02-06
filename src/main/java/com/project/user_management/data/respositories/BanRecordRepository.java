package com.project.user_management.data.respositories;

import com.project.user_management.data.models.BanRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BanRecordRepository extends JpaRepository<BanRecord, Long> {
}