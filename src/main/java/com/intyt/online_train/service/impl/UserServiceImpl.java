package com.intyt.online_train.service.impl;

import com.intyt.online_train.domain.Role;
import com.intyt.online_train.domain.RoleRepository;
import com.intyt.online_train.domain.User;
import com.intyt.online_train.domain.UserRepository;
import com.intyt.online_train.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    private UserRepository userRepository;
    private RoleRepository roleRepository;

    @Override
    @Autowired
    public void setRoleRepository(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        return userRepository.findFirstByUsernameAndIsDeleted(s, 0);
    }

    // 所有用户总数
    @Override
    public Long count() {
        return userRepository.count();
    }

    // 获取当前用户
    @Override
    public User current() {
        Object user = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (user == null) {
            return null;
        }
        if (user instanceof User) {
            return (User) user;
        } else {
            return null;
        }
    }

    // 通过id获取用户
    @Override
    public User get(Long id) {
        return userRepository.findFirstByIdAndIsDeleted(id, 0);
    }

    // 通过username获取用户
    @Override
    public User getByUsername(String username) {
        return userRepository.findFirstByUsernameAndIsDeleted(username, 0);
    }

    // 获取用户列表
    @Override
    public Page<User> list(Date beginTime, Date endTime, Role role, String keyword, Integer page, Integer pageSize) {
        Specification<User> specification = new Specification<User>() {
            @Override
            public Predicate toPredicate(Root<User> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicateList = new ArrayList<>();
                predicateList.add(criteriaBuilder.equal(root.get("isDeleted").as(Integer.class), 0));
                if (beginTime != null) {
                    predicateList.add(criteriaBuilder.greaterThanOrEqualTo(root.get("createTime").as(Date.class), beginTime));
                }
                if (endTime != null) {
                    predicateList.add(criteriaBuilder.lessThanOrEqualTo(root.get("createTime").as(Date.class), endTime));
                }
                if (role != null) {
                    predicateList.add(criteriaBuilder.isNotNull(root.get("role")));
                    predicateList.add(criteriaBuilder.equal(root.get("role").as(Role.class), role));
                }
                if (keyword != null && !keyword.isEmpty()) {
                    String likeWord = "%" + keyword + "%";
                    predicateList.add(criteriaBuilder.like(root.get("name").as(String.class), likeWord));
                }
                Predicate[] arrayType = new Predicate[predicateList.size()];
                return criteriaBuilder.and(predicateList.toArray(arrayType));
            }
        };
        Sort sort = new Sort(Sort.Direction.ASC, "id");
        if (page < 1) {
            page = 1;
        }
        return userRepository.findAll(specification, PageRequest.of(page - 1, pageSize, sort));
    }

    // 修改密码
    @Override
    public User changePassword(User user, String newPassword) throws Exception {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        user.setPassword(encoder.encode(newPassword));
        userRepository.save(user);
        return user;
    }

    // 新增用户
    @Override
    public User create(String username, String password, String name, Long roleId) throws Exception {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        User user = new User();
        user.setUsername(username);
        user.setPassword(encoder.encode(password));
        user.setName(name);
        user.setRole(roleRepository.findFirstById(roleId));
        user.setIsDeleted(0);
        user.setCreateTime(new Date());
        userRepository.save(user);
        return user;
    }

    // 修改用户
    @Override
    public User update(User user, String name, String password, Role role) {
        if (name != null) {
            user.setName(name);
        }
        if (password != null) {
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            user.setPassword(encoder.encode(password));
        }
        if (role != null) {
            user.setRole(role);
        }
        userRepository.save(user);
        return user;
    }

    // 删除用户
    @Override
    public void remove(User user) {
        user.setIsDeleted(1);
        userRepository.save(user);
    }

    // role角色 初始化专用
    @Override
    public Long countRole() {
        return roleRepository.count();
    }

    @Override
    public Role getRole(Long id) {
        return roleRepository.findFirstById(id);
    }

    @Override
    public Role getRole(String name) {
        return roleRepository.findFirstByName(name);
    }

    @Override
    public List<Role> roleList() {
        return roleRepository.findAll();
    }

    @Override
    public void createRole(String name) {
        Role role = new Role();
        role.setName(name);
        roleRepository.save(role);
    }
}
