package com.parker.common.jpa.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "file")
@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class FileEntity extends BaseInfoEntity {
    @Id
    @Column(name = "file_seq")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long fileSeq;
    @Column(name = "name")
    private String name;
    @Column(name = "category_seq")
    private Long categorySeq;
    @Column(name = "filePath")
    private String filePath;
    @Column(name = "fileSize")
    private Long fileSize;
}
