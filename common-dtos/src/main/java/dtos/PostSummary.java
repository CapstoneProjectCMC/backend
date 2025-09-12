package dtos;

public record PostSummary(
    String id,
    String title,
    String postType,
    boolean isPublic,
    String orgId
) {
}