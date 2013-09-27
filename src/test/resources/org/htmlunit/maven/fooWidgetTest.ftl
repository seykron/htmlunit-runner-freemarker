<#import "fooWidget.ftl" as widget />

<div id="js-dummy">
  <@widget.dummy ["John", "Joe", "Moe"] />
</div>

<script type="text/javascript">
  var foo = document.getElementById("js-dummy");
  var items = foo.getElementsByTagName("li");

  if (items[0].innerHTML !== "John") {
    throw new Error("John doesn't exist");
  }
  if (items[1].innerHTML !== "Joe") {
    throw new Error("Joe doesn't exist");
  }
  if (items[2].innerHTML !== "Moe") {
    throw new Error("Moe doesn't exist");
  }
  console.log("Succeed:");
  console.log(foo.innerHTML);
  window.close();
</script>
