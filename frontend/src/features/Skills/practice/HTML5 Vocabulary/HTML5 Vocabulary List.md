# HTML5 Vocabulary List

## 1. Document Structure

| Tag               | To-the-point Explanation       |
| ----------------- | ------------------------------ |
| `<!DOCTYPE html>` | Declares an HTML5 document.    |
| `<html>`          | Root element of the page.      |
| `<head>`          | Stores metadata and resources. |
| `<title>`         | Browser tab title.             |
| `<body>`          | Visible page content.          |

---

## 2. Metadata

| Tag          | To-the-point Explanation                 |
| ------------ | ---------------------------------------- |
| `<meta>`     | Metadata about the page.                 |
| `<link>`     | Links external resources (CSS, icons).   |
| `<style>`    | Internal CSS.                            |
| `<script>`   | JavaScript code or file.                 |
| `<base>`     | Base URL for relative links.             |
| `<noscript>` | Content shown if JavaScript is disabled. |

---

## 3. Text Content

| Tag            | To-the-point Explanation          |
| -------------- | --------------------------------- |
| `<h1>`–`<h6>`  | Heading levels.                   |
| `<p>`          | Paragraph.                        |
| `<br>`         | Line break.                       |
| `<hr>`         | Thematic separator.               |
| `<pre>`        | Preserves spaces and line breaks. |
| `<blockquote>` | Long quotation.                   |
| `<q>`          | Short inline quotation.           |
| `<abbr>`       | Abbreviation.                     |
| `<cite>`       | Title of a creative work.         |

---

## 4. Text Formatting

| Tag        | To-the-point Explanation |
| ---------- | ------------------------ |
| `<b>`      | Stylistically bold text. |
| `<strong>` | Important text.          |
| `<i>`      | Alternate voice or term. |
| `<em>`     | Emphasized text.         |
| `<u>`      | Underlined text.         |
| `<mark>`   | Highlighted text.        |
| `<small>`  | Small print.             |
| `<sub>`    | Subscript.               |
| `<sup>`    | Superscript.             |
| `<del>`    | Deleted text.            |
| `<ins>`    | Inserted text.           |
| `<code>`   | Code snippet.            |
| `<kbd>`    | Keyboard input.          |
| `<samp>`   | Program output.          |
| `<var>`    | Variable name.           |

---

## 5. Links & Navigation

| Tag   | To-the-point Explanation |
| ----- | ------------------------ |
| `<a>` | Hyperlink.               |

---

## 6. Images & Media

| Tag         | To-the-point Explanation    |
| ----------- | --------------------------- |
| `<img>`     | Displays an image.          |
| `<picture>` | Responsive image container. |
| `<source>`  | Media source.               |
| `<audio>`   | Audio player.               |
| `<video>`   | Video player.               |
| `<track>`   | Video subtitles/captions.   |
| `<iframe>`  | Embeds another webpage.     |

---

## 7. Lists

| Tag    | To-the-point Explanation |
| ------ | ------------------------ |
| `<ul>` | Unordered list.          |
| `<ol>` | Ordered list.            |
| `<li>` | List item.               |
| `<dl>` | Description list.        |
| `<dt>` | Description term.        |
| `<dd>` | Description details.     |

---

## 8. Tables

| Tag          | To-the-point Explanation |
| ------------ | ------------------------ |
| `<table>`    | Creates a table.         |
| `<caption>`  | Table title.             |
| `<thead>`    | Table header.            |
| `<tbody>`    | Table body.              |
| `<tfoot>`    | Table footer.            |
| `<tr>`       | Table row.               |
| `<th>`       | Header cell.             |
| `<td>`       | Data cell.               |
| `<colgroup>` | Groups columns.          |
| `<col>`      | Defines a column.        |

---

## 9. Forms

| Tag          | To-the-point Explanation |
| ------------ | ------------------------ |
| `<form>`     | Form container.          |
| `<label>`    | Input label.             |
| `<input>`    | User input field.        |
| `<textarea>` | Multi-line input.        |
| `<button>`   | Clickable button.        |
| `<select>`   | Drop-down list.          |
| `<option>`   | Drop-down option.        |
| `<optgroup>` | Groups options.          |
| `<fieldset>` | Groups form controls.    |
| `<legend>`   | Fieldset title.          |
| `<datalist>` | Input suggestions.       |
| `<output>`   | Calculation result.      |
| `<meter>`    | Scalar measurement.      |
| `<progress>` | Progress indicator.      |

### Common `<input>` Types

`text`, `password`, `email`, `number`, `tel`, `url`, `search`, `date`, `time`, `datetime-local`, `month`, `week`, `color`, `range`, `checkbox`, `radio`, `file`, `hidden`, `submit`, `reset`, `button`, `image`

---

## 10. Semantic Layout

| Tag            | To-the-point Explanation    |
| -------------- | --------------------------- |
| `<header>`     | Introductory content.       |
| `<nav>`        | Navigation links.           |
| `<main>`       | Main page content.          |
| `<section>`    | Themed section.             |
| `<article>`    | Independent content.        |
| `<aside>`      | Related or sidebar content. |
| `<footer>`     | Footer content.             |
| `<figure>`     | Self-contained media.       |
| `<figcaption>` | Figure caption.             |
| `<details>`    | Expandable content.         |
| `<summary>`    | Details heading.            |
| `<dialog>`     | Dialog box.                 |
| `<address>`    | Contact information.        |
| `<time>`       | Date or time value.         |

---

## 11. Generic Containers

| Tag      | To-the-point Explanation |
| -------- | ------------------------ |
| `<div>`  | Block-level container.   |
| `<span>` | Inline container.        |

---

## 12. Graphics

| Tag        | To-the-point Explanation  |
| ---------- | ------------------------- |
| `<canvas>` | Drawing surface.          |
| `<svg>`    | Scalable vector graphics. |

---

## 13. Templates & Web Components

| Tag          | To-the-point Explanation           |
| ------------ | ---------------------------------- |
| `<template>` | Hidden reusable content.           |
| `<slot>`     | Placeholder for component content. |

---

# Common Global Attributes

| Attribute         | To-the-point Explanation      |
| ----------------- | ----------------------------- |
| `id`              | Unique identifier.            |
| `class`           | CSS/JS grouping.              |
| `style`           | Inline CSS.                   |
| `title`           | Tooltip text.                 |
| `lang`            | Document language.            |
| `hidden`          | Hides the element.            |
| `tabindex`        | Keyboard navigation order.    |
| `contenteditable` | Makes content editable.       |
| `draggable`       | Enables dragging.             |
| `spellcheck`      | Enables spell checking.       |
| `translate`       | Allows or blocks translation. |
| `data-*`          | Stores custom data.           |
| `aria-*`          | Accessibility information.    |

---

# Common Element-Specific Attributes

| Attribute      | To-the-point Explanation       |
| -------------- | ------------------------------ |
| `src`          | Resource URL.                  |
| `href`         | Link URL.                      |
| `alt`          | Image description.             |
| `width`        | Width.                         |
| `height`       | Height.                        |
| `name`         | Element name.                  |
| `value`        | Element value.                 |
| `type`         | Input/button type.             |
| `placeholder`  | Input hint.                    |
| `required`     | Field is mandatory.            |
| `disabled`     | Disables interaction.          |
| `readonly`     | Read-only input.               |
| `checked`      | Checked state.                 |
| `selected`     | Selected option.               |
| `multiple`     | Allows multiple values.        |
| `autocomplete` | Browser autofill.              |
| `action`       | Form submission URL.           |
| `method`       | HTTP method (`GET`/`POST`).    |
| `target`       | Where to open the result.      |
| `loading`      | Image/iframe loading behavior. |

---

## Summary

An advanced HTML developer is comfortable with approximately:

* **~110 HTML elements**
* **~20–22 input types**
* **~25 global attributes**
* **~30–50 common element-specific attributes**

This is the core HTML5 vocabulary used to build modern, semantic, and accessible web pages.
