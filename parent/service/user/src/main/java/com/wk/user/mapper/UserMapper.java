package com.wk.user.mapper;

import com.wk.user.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

@Repository
public interface UserMapper extends BaseMapper<User> {

    @Update("UPDATE tb_user SET points=points+#{point} WHERE  username=#{username}")
    void addPoint(@Param("username") String username, @Param("point") Integer pint);
}
