{{/* vim: set filetype=mustache: */}}
{{/*
Expand the name of the chart.
*/}}
{{- define "tiamat.name" -}}
{{- default .Chart.Name .Values.nameOverride | trunc 63 | trimSuffix "-" -}}
{{- end -}}

{{/*
Create a default fully qualified app name.
We truncate at 63 chars because some Kubernetes name fields are limited to this (by the DNS naming spec).
*/}}
{{- define "tiamat.fullname" -}}
{{- $name := default .Chart.Name .Values.nameOverride -}}
{{- printf "%s-%s" .Release.Name $name | trunc 63 | trimSuffix "-" -}}
{{- end -}}

{{/* Generate basic labels */}}
{{- define "common.labels" }}
app: {{ .Values.app }}
version: {{ .Chart.Version }}
team: {{ .Values.team }}
slack: {{ .Values.slack }}
type: {{ .Values.type }}
chart: {{ .Chart.Name }}
release: {{ .Release.Name }}
api: {{ .Values.api }}
{{- end }}