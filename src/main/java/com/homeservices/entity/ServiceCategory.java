package com.homeservices.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "service_categories", uniqueConstraints = @UniqueConstraint(name = "uk_category_slug", columnNames = "slug"))
public class ServiceCategory {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 80)
    private String name;
    @Column(nullable = false, length = 90)
    private String slug;
    @Column(length = 250)
    private String description;
    @Column(length = 30)
    private String icon;
    @Column(nullable = false)
    private boolean active = true;

    public ServiceCategory() { }
    public ServiceCategory(String name, String slug, String description, String icon) {
        this.name = name; this.slug = slug; this.description = description; this.icon = icon; this.active = true;
    }
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getSlug() { return slug; }
    public void setSlug(String slug) { this.slug = slug; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getIcon() { return icon; }
    public void setIcon(String icon) { this.icon = icon; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}
