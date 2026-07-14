package com.machy.auth.service;

import com.machy.auth.entity.Config;
import com.machy.auth.repository.ConfigRepository;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class ConfigService {

    private final ConfigRepository configRepository;

    public ConfigService(ConfigRepository configRepository) {
        this.configRepository = configRepository;
    }

    public Map<String, String> getAll() {
        Map<String, String> result = new LinkedHashMap<>();
        for (Config c : configRepository.findAll()) {
            result.put(c.getClave(), c.getValor());
        }
        return result;
    }

    public Map<String, String> saveAll(Map<String, String> data) {
        for (Map.Entry<String, String> entry : data.entrySet()) {
            Config config = configRepository.findById(entry.getKey()).orElse(new Config());
            config.setClave(entry.getKey());
            config.setValor(entry.getValue());
            configRepository.save(config);
        }
        return getAll();
    }
}
