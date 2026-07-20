using System.ComponentModel.DataAnnotations;

namespace PackagingPortal.Api.DTOs;

public class CreateEnquiryDto
{
    [Required(ErrorMessage = "Name is required")]
    [MaxLength(100)]
    public string Name { get; set; } = string.Empty;

    [MaxLength(200)]
    public string? CompanyName { get; set; }

    [Required(ErrorMessage = "Phone number is required")]
    [MaxLength(20)]
    public string PhoneNumber { get; set; } = string.Empty;

    [Required(ErrorMessage = "Email is required")]
    [MaxLength(200)]
    [EmailAddress(ErrorMessage = "Invalid email format")]
    public string Email { get; set; } = string.Empty;

    [Required(ErrorMessage = "Subject is required")]
    [MaxLength(300)]
    public string Subject { get; set; } = string.Empty;

    [Required(ErrorMessage = "Message is required")]
    public string Message { get; set; } = string.Empty;
}

public class EnquiryResponseDto
{
    public int Id { get; set; }
    public string Name { get; set; } = string.Empty;
    public string? CompanyName { get; set; }
    public string PhoneNumber { get; set; } = string.Empty;
    public string Email { get; set; } = string.Empty;
    public string Subject { get; set; } = string.Empty;
    public string Message { get; set; } = string.Empty;
    public string Status { get; set; } = string.Empty;
    public string? Notes { get; set; }
    public DateTime CreatedAt { get; set; }
}

public class UpdateStatusDto
{
    [Required]
    [MaxLength(50)]
    public string Status { get; set; } = string.Empty;
}

public class UpdateNotesDto
{
    public string? Notes { get; set; }
}