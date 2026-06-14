using System.ComponentModel.DataAnnotations;

namespace PackagingPortal.Api.Models;

public class AuditLog
{
    [Key]
    public int Id { get; set; }

    public int? UserId { get; set; }

    [Required]
    [MaxLength(200)]
    public string Action { get; set; } = string.Empty;

    [Required]
    [MaxLength(100)]
    public string EntityType { get; set; } = string.Empty;

    public int? EntityId { get; set; }

    public DateTime Timestamp { get; set; } = DateTime.UtcNow;
}