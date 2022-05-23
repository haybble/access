package com.haybble.access.repository;

import com.haybble.access.entity.BlockedIpTable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BlockedIpRepository extends JpaRepository<BlockedIpTable, Integer> {
}
