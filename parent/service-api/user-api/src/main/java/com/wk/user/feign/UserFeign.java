package com.wk.user.feign;

import com.wk.user.entity.User;
import entity.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient("user")
@RequestMapping("user")
public interface UserFeign {

    /**
     * 用户下单后添加积分
     * @param point
     * @return
     */
    @GetMapping(value = "/points/add")
    Result addPoint(@RequestParam Integer point);

    /***
     * 根据ID查询User数据
     * @param id
     * @return
     */
    @GetMapping({"/load/{id}"})
    Result<User> findById(@PathVariable String id);

}
