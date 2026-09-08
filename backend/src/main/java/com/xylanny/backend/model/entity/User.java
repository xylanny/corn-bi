package com.xylanny.backend.model.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 用户表
 * @TableName user
 */
@TableName(value ="user")
@Data
public class User implements Serializable {
    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户昵称
     */
    private String userName;

    /**
     * 用户邮箱
     */
    private String userEmail;

    /**
     * 密码
     */
    private String userPassword;

    /**
     * 用户头像
     */
    private String userAvatar;

    /**
     * 用户角色: user/admin
     */
    private String userRole;

    /**
     * 创建时间
     */
    // 自动填充时间
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 是否删除 (0-未删除, 1-已删除)
     */
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
/**
 * 安装MybatisX-Generator插件，在IDEA中的“Database”项中选定需要自动生成entity、mapper、service的表
 * 注意在第二步勾选“lombok”、不生成toString()项
 * 将生成的entity、mapper、service放到项目对应位置
 *
 * 创建BusinessCode枚举类
 * 创建BusinessException类封装RuntimeException类，以便携带业务状态码
 * 创建全局异常处理器，统一捕获项目中抛出的各种异常，并转换成规范的JSON格式返回给前端，方便在Controller中专注业务逻辑
 *
 * 编写UserService类以及UserServiceImpl类
 * 创建UserVO类，用于去除密码这一敏感信息
 * 注意虽然给数据库中的表中的时间字段加上了默认值，但是无法像id返回注入到User实例中，因此我们在service中手动添加时间
 */