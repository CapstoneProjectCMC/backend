package com.codecampus.identity.dto.request.org;

import com.codecampus.identity.dto.request.authentication.UserCreationRequest;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BulkUserCreationRequest {
  /**
   * Vai trò hệ thống (User.roles) cho user mới: ADMIN | TEACHER | STUDENT
   * Nếu null => mặc định STUDENT.
   */
  String defaultRole;

  /**
   * Gắn membership theo scope. Có thể truyền:
   * - orgId (Organization) và/hoặc blockId (Grade)
   * -> nếu truyền cả 2: sẽ add vào Organization trước, sau đó add vào Block.
   */
  String orgId;
  String orgMemberRole; // "Admin" | "Teacher" | "Student" (mặc định "Student")

  String blockId;
  String blockRole;     // nếu null -> kế thừa orgMemberRole

  /**
   * Danh sách user sẽ được tạo.
   */
  List<UserCreationRequest> users;
}