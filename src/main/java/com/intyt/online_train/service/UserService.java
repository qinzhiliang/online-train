package com.intyt.online_train.service;

import com.intyt.online_train.domain.Role;
import com.intyt.online_train.domain.RoleRepository;
import com.intyt.online_train.domain.User;
import com.intyt.online_train.domain.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2018/8/22 0022.
 */
public interface UserService extends UserDetailsService {
    @Autowired
    void setRoleRepository(RoleRepository roleRepository);

    @Autowired
    void setUserRepository(UserRepository userRepository);

    @Override
    UserDetails loadUserByUsername(String s) throws UsernameNotFoundException;

    // 所有用户总数
    Long count();

    // 获取当前用户
    User current();

    // 通过id获取用户
    User get(Long id);

    // 通过username获取用户
    User getByUsername(String username);

    // 获取用户列表
    Page<User> list(Date beginTime, Date endTime, Role role, String keyword, Integer page, Integer pageSize);

    // 修改密码
    User changePassword(User user, String newPassword) throws Exception;

    // 新增用户
    User create(String username, String password, String name, Long roleId) throws Exception;

    // 修改用户
    User update(User user, String name, String password, Role role);

    // 删除用户
    void remove(User user);

    // role角色 初始化专用
    Long countRole();

    Role getRole(Long id);

    Role getRole(String name);

    List<Role> roleList();

    void createRole(String name);
}
