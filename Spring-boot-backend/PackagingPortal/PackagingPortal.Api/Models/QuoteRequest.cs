using System.ComponentModel.DataAnnotations;

namespace PackagingPortal.Api.Models;

public class QuoteRequest
{
    [Key]
    public int Id { get; set; }

    [Required]
    [MaxLength(100)]
    public string Name { get; set; } = string.Empty;

    [MaxLength(200)]
    public string? CompanyName { get; set; }

    [Required]
    [MaxLength(20)]
    public string PhoneNumber { get; set; } = string.Empty;

    [Required]
    [MaxLength(200)]
    [EmailAddress]
    public string Email { get; set; } = string.Empty;

    [Required]
    [MaxLength(100)]
    public string ProductType { get; set; } = string.Empty;

    [Required]
    public decimal Length { get; set; }

    [Required]
    public decimal Width { get; set; }

    [Required]
    public decimal Height { get; set; }

    [Required]
    public int Quantity { get; set; }

    [MaxLength(100)]
    public string? Color { get; set; }

    public bool PrintingRequired { get; set; }

    [Required]
    [MaxLength(300)]
    public string DeliveryLocation { get; set; } = string.Empty;

    [Required]
    [MaxLength(200)]
    public string ExpectedTimeline { get; set; } = string.Empty;

    public string? AdditionalNotes { get; set; }

    [MaxLength(500)]
    public string? ReferenceImagePath { get; set; }

    [MaxLength(500)]
    public string? ArtworkPath { get; set; }

    [MaxLength(50)]
    public string Status { get; set; } = "NEW";

    public string? Notes { get; set; }

    public DateTime CreatedAt { get; set; } = DateTime.UtcNow;
}