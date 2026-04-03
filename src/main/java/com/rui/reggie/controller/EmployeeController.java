package com.rui.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rui.reggie.common.BaseContext;
import com.rui.reggie.common.Result;
import com.rui.reggie.entity.Employee;
import com.rui.reggie.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Value("${reggie.default-password}")
    private String defaultPassword;

    /**
     * 登录
     *
     * @param request
     * @param employee
     * @return
     */

    @PostMapping("/login")
    public Result<Employee> login(HttpServletRequest request, @RequestBody Employee employee) {
        String rawPassword = employee.getPassword();

        // 根据用户名查询数据库
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername, employee.getUsername());
        Employee emp = employeeService.getOne(queryWrapper);

        if (emp == null) {
            return Result.error("用户不存在");
        }

        // 查看状态是否禁用
        if (emp.getStatus() == 0) {
            return Result.error("账号已禁用");
        }

        String storedPassword = emp.getPassword();
        boolean passwordValid = false;

        // 1. 首先尝试BCrypt验证（新密码）
        try {
            passwordValid = passwordEncoder.matches(rawPassword, storedPassword);
        } catch (Exception e) {
            log.debug("BCrypt验证失败，尝试MD5验证");
        }

        // 2. 如果BCrypt失败，尝试MD5验证（旧密码兼容）
        if (!passwordValid && storedPassword != null && storedPassword.length() == 32) {
            // 假设MD5哈希长度为32字符
            String md5Password = DigestUtils.md5DigestAsHex(rawPassword.getBytes());
            passwordValid = storedPassword.equals(md5Password);

            // 如果MD5验证成功，升级密码为BCrypt
            if (passwordValid) {
                emp.setPassword(passwordEncoder.encode(rawPassword));
                BaseContext.setThreadLocalId(emp.getId());
                try {
                    employeeService.updateById(emp);
                    log.info("用户 {} 密码已从MD5升级为BCrypt", emp.getUsername());
                } finally {
                    BaseContext.remove();
                }
            }
        }

        if (!passwordValid) {
            return Result.error("密码错误");
        }

        // 登陆成功
        request.getSession().setAttribute("employee", emp.getId());
        return Result.success(emp);
    }

    /**
     * 退出登录
     *
     * @param request
     * @return
     */
    @PostMapping("/logout")
    public Result<String> logout(HttpServletRequest request) {
        // 清理Session中保存当前登录员工的id
        request.getSession().removeAttribute("employee");
        return Result.success("退出成功");
    }

    /**
     * 新增员工
     *
     * @param request
     * @param employee
     * @return
     */
    @PostMapping("/save")
    public Result<String> save(HttpServletRequest request, @RequestBody Employee employee) {
        // 设置初始密码，使用BCrypt加密
        employee.setPassword(passwordEncoder.encode(defaultPassword));


        employeeService.save(employee);
        return Result.success("新增成功");
    }

    /**
     * 员工分页查询
     *
     * @param page
     * @param pageSize
     * @param name
     * @return
     */

    @GetMapping("/page")
    public Result<Page<Employee>> page(int page, int pageSize, String name) {

        // 构造分页构造器
        Page<Employee> pageInfo = new Page<>(page, pageSize);
        // 构造条件
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.isNotEmpty(name), Employee::getName, name);

        // 添加排序条件
        queryWrapper.orderByDesc(Employee::getUpdateTime);

        // 执行查询
        employeeService.page(pageInfo, queryWrapper);

        return Result.success(pageInfo);
    }

    @PostMapping("/update")
    public Result<String> update(@RequestBody Employee employee) {
        log.info("更新员工信息: {}", employee.getId());
        employeeService.updateById(employee);
        return Result.success("修改成功");
    }

    @GetMapping("/query")
    public Result<Employee> getEmployeeByName(String name) {
        log.info(name);
        Employee employee = employeeService.getEmployeeByName(name);
        return Result.success(employee);
    }
}
