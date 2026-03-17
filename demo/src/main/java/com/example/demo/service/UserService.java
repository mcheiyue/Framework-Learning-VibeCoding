package com.example.demo.service;

import com.example.demo.dto.UserRegisterDTO;
import com.example.demo.entity.User;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class UserService {

	private final ConcurrentHashMap<Long, User> storage = new ConcurrentHashMap<>();
	private final AtomicLong idGenerator = new AtomicLong(0L);

	public UserService() {
		initData();
	}

	public User getById(Long id) {
		return storage.get(id);
	}

	public List<User> list() {
		return storage.values().stream()
				.sorted(Comparator.comparing(User::getId))
				.toList();
	}

	public User register(UserRegisterDTO dto) {
		Long id = idGenerator.incrementAndGet();

		User user = new User();
		user.setId(id);
		user.setUsername(dto.getUsername());
		user.setPassword(dto.getPassword());
		user.setEmail(dto.getEmail());
		user.setPhone(dto.getPhone());
		user.setAge(dto.getAge());
		user.setStatus(1);

		LocalDateTime now = LocalDateTime.now();
		user.setCreateTime(now);
		user.setUpdateTime(now);

		storage.put(id, user);
		return user;
	}

	private void initData() {
		LocalDateTime now = LocalDateTime.now();

		User user1 = new User();
		user1.setId(1L);
		user1.setUsername("张三");
		user1.setEmail("zhangsan@example.com");
		user1.setAge(20);
		user1.setStatus(1);
		user1.setCreateTime(now);
		user1.setUpdateTime(now);
		storage.put(1L, user1);

		User user2 = new User();
		user2.setId(2L);
		user2.setUsername("李四");
		user2.setEmail("lisi@example.com");
		user2.setAge(22);
		user2.setStatus(1);
		user2.setCreateTime(now);
		user2.setUpdateTime(now);
		storage.put(2L, user2);

		idGenerator.set(2L);
	}
}
