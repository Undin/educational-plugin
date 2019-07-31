package com.jetbrains.edu.learning.yaml

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonPropertyOrder
import com.jetbrains.edu.coursecreator.yaml.format.ChoiceTaskYamlMixin
import com.jetbrains.edu.coursecreator.yaml.format.ChoiceTaskYamlMixin.Companion.FEEDBACK_CORRECT
import com.jetbrains.edu.coursecreator.yaml.format.ChoiceTaskYamlMixin.Companion.FEEDBACK_INCORRECT
import com.jetbrains.edu.coursecreator.yaml.format.ChoiceTaskYamlMixin.Companion.IS_MULTIPLE_CHOICE
import com.jetbrains.edu.coursecreator.yaml.format.ChoiceTaskYamlMixin.Companion.OPTIONS
import com.jetbrains.edu.coursecreator.yaml.format.TaskYamlMixin.Companion.FEEDBACK_LINK
import com.jetbrains.edu.coursecreator.yaml.format.TaskYamlMixin.Companion.FILES
import com.jetbrains.edu.coursecreator.yaml.format.TaskYamlMixin.Companion.TYPE
import com.jetbrains.edu.learning.courseFormat.CheckStatus

private const val SELECTED_VARIANTS = "selected_variants"
private const val STATUS = "status"
private const val RECORD = "record"

@JsonPropertyOrder(TYPE, IS_MULTIPLE_CHOICE, OPTIONS, FEEDBACK_CORRECT, FEEDBACK_INCORRECT, FILES, FEEDBACK_LINK,
                   SELECTED_VARIANTS, STATUS, RECORD)
@Suppress("UNUSED_PARAMETER", "unused") // used for yaml serialization
class EduChoiceTaskYamlMixin: ChoiceTaskYamlMixin() {
  @JsonProperty(SELECTED_VARIANTS)
  var selectedVariants = mutableListOf<Int>()

  @JsonProperty(STATUS)
  private lateinit var myStatus: CheckStatus

  @JsonProperty(RECORD)
  private var myRecord: Int = -1
}