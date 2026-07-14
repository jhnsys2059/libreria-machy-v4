package com.machy.auth.repository;

import com.machy.auth.entity.Config;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConfigRepository extends JpaRepository<Config, String> {
}
