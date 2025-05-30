package com.project.model

import com.project.enums.JobStatus
import org.joda.time.DateTime

import java.util.UUID

case class Job(
              id: UUID,
              project_id: UUID,
              name: String,
              description: Option[String],
              status: JobStatus,
              due_date: Option[DateTime],
              created_at: DateTime = DateTime.now(),
              updated_at: DateTime = DateTime.now()
              )
