package com.orastays.testimonialserver.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.orastays.testimonialserver.helper.DbConnection;

@Repository
public class BaseDAO {

	@Autowired
	protected DbConnection dbConnection;
}
