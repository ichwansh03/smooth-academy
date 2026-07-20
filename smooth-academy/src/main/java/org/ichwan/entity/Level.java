package org.ichwan.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "levels")
public class Level {

    @Id
    private Integer id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String icon;

    @Column(nullable = false)
    private String label;

    @Column(name = "min_range", nullable = false)
    private int minRange;

    @Column(name = "max_range", nullable = false)
    private int maxRange;

    @Column(name = "required_stars", nullable = false)
    private int requiredStars;

    @Column(name = "sort_order", nullable = false)
    private int sortOrder;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getIcon() { return icon; }
    public void setIcon(String icon) { this.icon = icon; }
    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = label; }
    public int getMinRange() { return minRange; }
    public void setMinRange(int minRange) { this.minRange = minRange; }
    public int getMaxRange() { return maxRange; }
    public void setMaxRange(int maxRange) { this.maxRange = maxRange; }
    public int getRequiredStars() { return requiredStars; }
    public void setRequiredStars(int requiredStars) { this.requiredStars = requiredStars; }
    public int getSortOrder() { return sortOrder; }
    public void setSortOrder(int sortOrder) { this.sortOrder = sortOrder; }
}
