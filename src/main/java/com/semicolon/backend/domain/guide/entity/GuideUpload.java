package com.semicolon.backend.domain.guide.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@ToString(exclude = "guide")
@Table(name="tbl_guide_file")

public class GuideUpload {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long fileNo;

    private String fileName;
    private String filePath;
    private String savedName;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "guide_id")
    private Guide guide;
}
