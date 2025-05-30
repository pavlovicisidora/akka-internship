package com.project.model

import java.util.UUID
import org.joda.time.DateTime

case class Workspace(
                    id: UUID,
                    name: String,
                    description: Option[String],
                    created_at: DateTime = DateTime.now(),
                    updated_at: DateTime = DateTime.now()
                    )
