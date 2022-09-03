package com.kg.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kg.backend.pojo.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}
