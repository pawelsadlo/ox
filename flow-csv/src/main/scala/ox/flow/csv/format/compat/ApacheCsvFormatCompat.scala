package ox.flow.csv.format.impl

import org.apache.commons.csv.CSVFormat.Builder as ApacheCSVBuilder
import org.apache.commons.csv.{DuplicateHeaderMode, CSVFormat as ApacheCSVFormat}
import ox.flow.csv.format.{CsvFormat, HeaderMode, QuoteMode}

private[csv] object ApacheCsvFormatCompat:
  /** Brings every Apache setting that is not part of our contract to a fixed value.
    *
    * Header handling is relaxed so that the parser never consumes, validates or rejects the first record: every record
    * reaches us untouched and [[HeaderMode]] alone decides what the first one is.
    *
    * The remaining settings are ones we do not expose at all
   *   pinning them keeps them in order to have consistent `equals`/`hashCode`.
    */
  private [csv] def overrideSettingsForCompatibility(apacheBuilder: ApacheCSVBuilder): ApacheCSVBuilder =
    apacheBuilder
      .setHeader(null.asInstanceOf[Array[String]]*)
      .setSkipHeaderRecord(false)
      .setDuplicateHeaderMode(DuplicateHeaderMode.ALLOW_ALL)
      .setAllowMissingColumnNames(true)
      .setHeaderComments(null.asInstanceOf[Array[String]]*)
      .setIgnoreHeaderCase(false)
      .setAutoFlush(false)
      .setMaxRows(0)


  /** Applies the settings to a newly created [[ApacheCSVBuilder]], which validates them as the format is constructed. */
  @throws[IllegalArgumentException]
  private[csv] def toApache(settings: CsvFormat.Settings): ApacheCSVFormat =
    val b = ApacheCSVBuilder.create()
    b.setCommentMarker(settings.commentMarker.map(Character.valueOf).orNull)
    b.setDelimiter(settings.delimiter)
    b.setEscape(settings.escape.map(Character.valueOf).orNull)
    b.setIgnoreEmptyLines(settings.ignoreEmptyLines)
    b.setIgnoreSurroundingSpaces(settings.ignoreSurroundingSpaces)
    b.setLenientEof(settings.lenientEof)
    b.setNullString(settings.nullString.orNull)
    b.setQuote(settings.quote.map(Character.valueOf).orNull)
    b.setQuoteMode(settings.quoteMode.map(_.toApache).orNull)
    b.setRecordSeparator(settings.recordSeparator)
    b.setTrailingData(settings.trailingData)
    b.setTrailingDelimiter(settings.trailingDelimiter)
    b.setTrim(settings.trim)

    overrideSettingsForCompatibility(b).get


  /** Reads the settings out of a predefined Apache format, relaxing the ones we take over. */
  private[csv] def settingsOf(format: ApacheCSVFormat, headerMode: HeaderMode): CsvFormat.Settings =
    val relaxed = overrideSettingsForCompatibility(format.builder()).get()
    CsvFormat.Settings(
      headerMode = headerMode,
      commentMarker = Option(relaxed.getCommentMarker).map(_.charValue),
      delimiter = relaxed.getDelimiterString,
      escape = Option(relaxed.getEscapeCharacter).map(_.charValue),
      ignoreEmptyLines = relaxed.getIgnoreEmptyLines,
      ignoreSurroundingSpaces = relaxed.getIgnoreSurroundingSpaces,
      lenientEof = relaxed.getLenientEof,
      nullString = Option(relaxed.getNullString),
      quote = Option(relaxed.getQuoteCharacter).map(_.charValue),
      quoteMode = Option(relaxed.getQuoteMode).map(QuoteMode.fromApache),
      recordSeparator = relaxed.getRecordSeparator,
      trailingData = relaxed.getTrailingData,
      trailingDelimiter = relaxed.getTrailingDelimiter,
      trim = relaxed.getTrim
    )
