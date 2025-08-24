package com.codecampus.identity.service.account;

import com.codecampus.identity.dto.request.authentication.RoleRequest;
import com.codecampus.identity.dto.response.authentication.PermissionResponse;
import com.codecampus.identity.dto.response.authentication.RoleResponse;
import com.codecampus.identity.entity.account.Permission;
import com.codecampus.identity.entity.account.Role;
import com.codecampus.identity.mapper.authentication.RoleMapper;
import com.codecampus.identity.repository.account.PermissionRepository;
import com.codecampus.identity.repository.account.RoleRepository;
import java.util.HashSet;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

/**
 * Dịch vụ quản lý vai trò (Role) trong hệ thống.
 *
 * <p>Cung cấp các phương thức để:
 * <ul>
 *   <li>Tạo vai trò mới với danh sách quyền liên quan.</li>
 *   <li>Lấy danh sách tất cả vai trò.</li>
 *   <li>Xóa vai trò dựa trên mã vai trò.</li>
 * </ul>
 * Tất cả các phương thức chỉ cho phép thực thi khi người dùng có vai trò ADMIN.
 * </p>
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RoleService {
  RoleRepository roleRepository;
  PermissionRepository permissionRepository;
  RoleMapper roleMapper;

  /**
   * Tạo mới một vai trò trong hệ thống, đồng thời gán các quyền tương ứng.
   *
   * @param roleRequest đối tượng RoleRequest chứa tên vai trò và danh sách quyền
   * @return RoleResponse chứa thông tin vai trò vừa được lưu
   */
  @PreAuthorize("hasRole('ADMIN')")
  public RoleResponse createRole(RoleRequest roleRequest) {
    Role role = roleMapper.toRole(roleRequest);

    List<Permission> permissions = permissionRepository
        .findAllById(roleRequest.getPermissions().stream()
            .map(PermissionResponse::getName).toList());
    role.setPermissions(new HashSet<>(permissions));

    role = roleRepository.save(role);
    return roleMapper.toRoleResponse(role);
  }

  /**
   * Lấy danh sách tất cả các vai trò hiện có trong hệ thống.
   *
   * @return danh sách RoleResponse tương ứng với mỗi vai trò
   */
  @PreAuthorize("hasRole('ADMIN')")
  public List<RoleResponse> getAllRoles() {
    return roleRepository.findAll()
        .stream()
        .map(roleMapper::toRoleResponse)
        .toList();
  }

  /**
   * Xóa một vai trò dựa trên tên vai trò.
   *
   * @param roleName tên vai trò cần xóa
   */
  @PreAuthorize("hasRole('ADMIN')")
  public void delete(String roleName) {
    roleRepository.deleteById(roleName);
  }
}
