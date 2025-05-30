package com.project.model

import com.project.enums.ProjectStatus
import org.joda.time.DateTime

import java.util.UUID

case class Project(
                    id: UUID,
                    workspace_id: UUID,
                    name: String,
                    description: Option[String],
                    status: ProjectStatus,
                    created_at: DateTime = DateTime.now(),
                    updated_at: DateTime = DateTime.now()
                  )
