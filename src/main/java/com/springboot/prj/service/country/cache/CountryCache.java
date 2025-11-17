package com.springboot.prj.service.country.cache;

import com.springboot.lib.cache.LazyCache;
import com.springboot.prj.service.country.CountryService;
import com.springboot.prj.service.country.dto.CountryDTO;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CountryCache extends LazyCache<List<CountryDTO>> {
    private final CountryService countryService;

    public CountryCache(CountryService countryService) {
        // Cache 5 phút (5 * 60 * 1000 ms)
        super(5 * 60 * 1000);
        this.countryService = countryService;
    }

    @Override
    public List<CountryDTO> load() {
        return countryService.getCountries();
    }
}
