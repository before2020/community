package com.wyli.community.dao;

import org.springframework.stereotype.Repository;

@Repository("lololo")
public class AlphaDaoMybatisImpl implements AlphaDao {
    @Override
    public String select() {
        return "Mybatis!!!";
    }
}
