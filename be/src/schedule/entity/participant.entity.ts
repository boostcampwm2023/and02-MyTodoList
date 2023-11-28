import { BaseEntity, Column, Entity, JoinColumn, ManyToOne, OneToOne, PrimaryGeneratedColumn } from "typeorm";
import { ScheduleMetadataEntity } from "./schedule-metadata.entity";

@Entity("participant")
export class ParticipantEntity extends BaseEntity {
  @PrimaryGeneratedColumn({ name: "participant_id" })
  participantId: number;

  @Column({ name: "participant_people_id" })
  participantPeopleId: number;

  @Column({ name: "author_id" })
  authorId: number;

  /*
   * relation
   */
  @OneToOne(() => ScheduleMetadataEntity, (scheduleMeta) => scheduleMeta.metadataId)
  @JoinColumn({ name: "participant_people_id" })
  participantPeople: ScheduleMetadataEntity;

  @ManyToOne(() => ScheduleMetadataEntity, (scheduleMeta) => scheduleMeta.author, {
    onDelete: "CASCADE",
  })
  @JoinColumn({ name: "author_id" })
  author: ScheduleMetadataEntity;
}
