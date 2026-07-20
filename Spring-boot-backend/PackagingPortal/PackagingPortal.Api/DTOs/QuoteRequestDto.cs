using System.ComponentModel.DataAnnotations;

namespace PackagingPortal.Api.DTOs;

public class CreateQuoteRequestDto
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

    [Required(ErrorMessage = "Product type is required")]
    [MaxLength(100)]
    public string ProductType { get; set; } = string.Empty;

    [Required(ErrorMessage = "Length is required")]
    [Range(0.1, double.MaxValue, ErrorMessage = "Length must be greater than 0")]
    public decimal Length { get; set; }

    [Required(ErrorMessage = "Width is required")]
    [Range(0.1, double.MaxValue, ErrorMessage = "Width must be greater than 0")]
    public decimal Width { get; set; }

    [Required(ErrorMessage = "Height is required")]
    [Range(0.1, double.MaxValue, ErrorMessage = "Height must be greater than 0")]
    public decimal Height { get; set; }

    [Required(ErrorMessage = "Quantity is required")]
    [Range(1, int.MaxValue, ErrorMessage = "Quantity must be at least 1")]
    public int Quantity { get; set; }

    [MaxLength(100)]
    public string? Color { get; set; }

    public bool PrintingRequired { get; set; }

    [Required(ErrorMessage = "Delivery location is required")]
    [MaxLength(300)]
    public string DeliveryLocation { get; set; } = string.Empty;

    [Required(ErrorMessage = "Expected timeline is required")]
    [MaxLength(200)]
    public string ExpectedTimeline { get; set; } = string.Empty;

    public string? AdditionalNotes { get; set; }
}

public class QuoteRequestResponseDto
{
    public int Id { get; set; }
    public string Name { get; set; } = string.Empty;
    public string? CompanyName { get; set; }
    public string PhoneNumber { get; set; } = string.Empty;
    public string Email { get; set; } = string.Empty;
    public string ProductType { get; set; } = string.Empty;
    public decimal Length { get; set; }
    public decimal Width { get; set; }
    public decimal Height { get; set; }
    public int Quantity { get; set; }
    public string? Color { get; set; }
    public bool PrintingRequired { get; set; }
    public string DeliveryLocation { get; set; } = string.Empty;
    public string ExpectedTimeline { get; set; } = string.Empty;
    public string? AdditionalNotes { get; set; }
    public string? ReferenceImagePath { get; set; }
    public string? ArtworkPath { get; set; }
    public string Status { get; set; } = string.Empty;
    public string? Notes { get; set; }
    public DateTime CreatedAt { get; set; }
}