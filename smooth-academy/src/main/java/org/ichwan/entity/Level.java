package org.ichwan.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "levels")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
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
    @Builder.Default
    private int requiredStars = 0;

    @Column(name = "sort_order", nullable = false)
    private int sortOrder;
}
