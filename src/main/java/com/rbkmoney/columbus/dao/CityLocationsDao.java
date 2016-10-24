package com.rbkmoney.columbus.dao;

import com.rbkmoney.columbus.model.CityLocation;
import com.rbkmoney.columbus.model.Lang;
import com.rbkmoney.columbus.exception.DaoException;

import java.util.List;
import java.util.Set;

public interface CityLocationsDao {

    CityLocation getByGeoId(int geoId, Lang lang) throws DaoException;
    List<CityLocation> getByGeoIds(Set<Integer> geoIdset, Lang lang) throws DaoException;

}

